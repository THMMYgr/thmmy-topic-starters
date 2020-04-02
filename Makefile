build:
	@docker-compose -p topicstarters build;
run:
	@docker-compose -p topicstarters up -d
stop:
	@docker-compose -p topicstarters down
clean-data: 
	@docker-compose -p topicstarters down -v
clean-images:
	@docker rmi `docker images -q -f "dangling=true"`
