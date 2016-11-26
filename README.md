# sneakpeek server
A server that enables to gather interest data from images. 
It consists of a simple json REST server that exposes all json files contained in a directory tree, plus a core algorithm that calculates a heatmap of the areas of interest of the image.

For correct functionality, it's recommended that the data is collected and sent through a *sneakpeek client*.

## Install
sudo npm install -g https://github.com/dshahrokhian/sneakpeek-server.git

### Options
#### --port (-P) <number>
Set a different port to the default 3000
```bash
$ json-rest-server myapi/interest_data -P 8080
```

#### --allowCORS (-C)
Enable cross-origin resource sharing (CORS), default is disabled
```bash
$ json-rest-server myapi/interest_data -C
```

## Example
Create a directory in which you want to receive the interest data.

```
myapi
├── interest_data/
```

Start your server

```bash
$ json-rest-server myapi/interest_data -P 8080 -C
```
After receiving interest data, your directory will look something like this:
```
myapi
├── interest_data/
│   ├── image1/
│   │	├── user1
│   │   ├── 1.json
│   │   │   2.json
│   │   ├── 3.json
│   │	├── user2
│   │   ├── 1.json
│   ├── image2/
...
```
Now if you go to [http://localhost:8080/image1/user2](), you'll get the content of 1.json inside user2

But if you go to [http://localhost:8080/image1/user1](), you'll get an array with the content of all json files that are in the user1 folder

## Routes
Like REST, you can have GET and POST on folders and GET, PUT and DELETE on json files. Based on previous example, you can have

```
GET    /image1/user1    -> ARRAY of json files conteined in myapi/interest_data/image1/user1
GET    /image1/user2    -> JSON content of myapi/image1/user2/1.json
POST   /image1/user2    -> CREATE a new json file in myapi/interest_data/image1/user2
PUT    /image1/user2    -> UPDATE content of myapi/image1/user2/1.json
PATCH  /image1/user2/1  -> UPDATE content of myapi/interest_data/image1/user2/1.json merging with existing content
DELETE /image1/user2/1  -> DELETE file myapi/interest_data/image1/user2/1.json
```
