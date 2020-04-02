#!/bin/sh

topic_starters_username=$(cat /run/secrets/topic_starters_username)
topic_starters_password=$(cat /run/secrets/topic_starters_password)

java -DTOPIC_STARTERS_USERNAME="$topic_starters_username" \
  -DTOPIC_STARTERS_PASSWORD="$topic_starters_password" \
  -jar app.jar gr.thmmy.mthmmy.topicstarter.api.TopicStarterApplication
