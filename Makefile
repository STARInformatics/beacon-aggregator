configure:
	cp -f server/src/main/resources/application.properties-template server/src/main/resources/application.properties
	cp -f server/src/main/resources/ogm.properties-template server/src/main/resources/ogm.properties

docker-build:
	docker-compose -f docker-compose.yml build

docker-run:
	docker-compose -f docker-compose.yml up

docker-stop:
	sudo docker-compose -f docker-compose.yml down
