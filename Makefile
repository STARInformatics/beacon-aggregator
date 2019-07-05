configure:
	cp -f server/src/main/resources/application.properties-template server/src/main/resources/application.properties
	cp -f server/src/main/resources/ogm.properties-template server/src/main/resources/ogm.properties

docker-build:
	docker-compose -f docker-compose.yaml build

docker-run:
	docker-compose -f docker-compose.yaml up

docker-stop:
	sudo docker-compose -f docker-compose.yaml down

docker-restart:
	sudo docker-compose -f docker-compose.yaml down
	sudo docker-compose -f docker-compose.yaml up

