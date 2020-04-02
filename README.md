# Thmmy Topic Starters
> A service that parses all the topics of thmmy.gr into a database and exposes an endpoint for getting filtered pages of them 

Thmmy topic starters is an application that crawls all thmmy.gr boards every day at 2 a.m. Information parsed are then saved in a
postgres database and can be accessed by the endpoint `/api/topicstarters`.

---

# API endpoint

## View topic starters

```
GET /api/topicstarters
```

### Parameters

| Name  | Type   | Description |
| ----- | ------ | ----------- |
| user  | String | **Optional**. The username or ID of the user. Filters the results by user. |
| board | String | **Optional**. The title or ID of the board. Filters the results by board. |
| topic | String | **Optional**. The subject or ID of the topic. Filters the results by topic. |

#### Example

```shell script
curl --location \
  --request GET 'localhost:8080/api/topicstarters' \
  --form 'user=14670' \
  --form 'board=Ανακοινώσεις και Έκτακτα νέα' \
  --form 'topic=68000'
```

### Response

```
Status: 200 OK
Content-Type: application/json;charset=UTF-8
Content-Length: 962
Content-Encoding: gzip
```
```json
{
    "content": [
        {
            "id": "d806599f-ae77-4780-bd3d-510943588054",
            "topicId": 68000,
            "topicUrl": "https://www.thmmy.gr/smf/index.php?topic=68000.0",
            "starterUsername": "Apostolof",
            "starterUrl": "https://www.thmmy.gr/smf/index.php?action=profile;u=14670",
            "starterId": 14670,
            "boardTitle": "Ανακοινώσεις και Έκτακτα νέα",
            "boardUrl": "https://www.thmmy.gr/smf/index.php?board=25.0",
            "boardId": 25,
            "topicSubject": "mTHMMY (alpha version)",
            "numberOfReplies": 175,
            "numberOfViews": 15729
        }
    ],
    "pageable": {
        [...]
    },
    [...]
}
```

\* part of the response truncated for brevity

--- 

# Build docker image

To build the docker image you first need to build the java application for production:
```shell script
mvn clean install package
```

Define a username, password and database name for the postgres database in the file `./env/topic_starters_postgres.env`.
An example of what this file might look like is given in `./env/topic_starters_postgres.example.env`.

If you want to get all the topics accessible by a logged-in user (rather that just those publicly available to guests) you also need to create two more files containing the username and password of a user for the application to use.
* `./secrets/username`: which should contain the username
* `./secrets/password`: which should contain the password

Then just use the Makefile to handle the build:
```shell script
make build
```

Run the image using:
```shell script
make run
```

Stop the container using:
```shell script
make stop
```

The Makefile also provides targets for cleaning the data and dangling images.

---

## License

[![Beerware License](https://img.shields.io/badge/license-beerware%20%F0%9F%8D%BA-blue.svg)](https://github.com/ThmmyNoLife/thmmy-topic-starters/blob/develop/LICENSE)
