## Description
Simple backend for note-taking, lets see where I take this

## Dependencies
- Docker
- JDK 17

## Running locally
1. Start the DB by running `docker-compose up`
2. Start the project! I prefer to use intelliJ and just running it from there


## API documentation
Have not been bothered creating swagger docs yet so for now

### Creating a note
You can create a note by calling the endpoint

`localhost:8080/api/v1/note` using `POST`

it expects a body of type

```
{
    title: string
    body: string
    tags: string
}
```

e.g.

```json
{
  "title": "Frank's memoir",
  "body": "When I'm dead, just throw me in the trash",
  "tags": "always,sunny,in,philadelphia"
}
```

### Listing notes
You can list the notes by calling the endpoint

`localhost:8080/api/v1/note` using `GET`

it will return a body of type
```
{
    [
        title: string
        body: string
        tags: string,
        parsedTags: Array<string>,
        createdAt: Date,
        updatedAt: Date,
        deletedAt: Date
    ]
}
```

e.g.

```json
[
  {
    "title": "Frank's memoir", 
    "body": "When I'm dead, just throw me in the trash", 
    "tags": "always,sunny,in,philadelphia",
    "parsedTags": [
      "always",
      "sunny",
      "in",
      "philadelphia"
    ],
    "createdAt": "2022-12-29",
    "updatedAt": null,
    "deletedAt": null
  }
]
```