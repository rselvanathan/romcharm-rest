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
docker run -d --name romcharm-rest \
-e VIRTUAL_HOST=api.romandcharmi.com \
-e LETSENCRYPT_HOST=api.romandcharmi.com \
-e LETSENCRYPT_EMAIL= \
-e jwtSecret= \
-e AWS_ACCESS_KEY_ID= \
-e AWS_SECRET_ACCESS_KEY= \
-e AWS_EMAIL_SNS_TOPIC= \
-e APP_TYPE=ROMCHARM \
-it rselvanathan/romcharm-rest:latest