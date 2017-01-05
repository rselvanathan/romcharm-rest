docker pull rselvanathan/romcharm-rest:latest
isImageRunning=$(docker inspect -f {{.State.Running}} romcharm-rest 2> /dev/null)
if [ "$isImageRunning" = "true" ]; then
	echo "Removing romcharm-rest container"
	docker stop romcharm-rest
	docker rm romcharm-rest
fi
value=$(docker images -q --filter "dangling=true")
if [ "$value" = "" ]; then
	echo "No Dangling Images"
else
	echo "Removing Dangling Images"
 	docker images -q --filter "dangling=true" | xargs docker rmi
fi
docker run -d -e VIRTUAL_HOST=api.romandcharmi.com  --name romcharm-rest --link mongoDB:mongodb -e MONGODB_DBNAME={} -e MONGODB_USERNAME={} -e MONGODB_PASSWORD={} -e jwtSecret={} -it rselvanathan/romcharm-rest:latest