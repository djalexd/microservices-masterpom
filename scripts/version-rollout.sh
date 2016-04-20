#!/bin/bash

usage="$0 --consul <addr> --app-name <name> --app-version <version> --git-commit <sha>"

CONSUL_ADDR="localhost:8500"
GIT_COMMIT="n/a"

# Get the  parameters from command line
while [ "$#" -gt 0 ]
do
	case "$1" in
	--consul)
		CONSUL_ADDR="$2" ;;
	--app-name)
		APP="$2" ;;
	--app-version)
		NEW_VERSION="$2" ;;
	--git-commit)
		GIT_COMMIT="$2" ;;
	*) ;; # skip the value of the option
	esac
	shift # move to the next arg
done

if [ -z $APP ]; then
  echo $usage
  exit 1
fi

if [ -z $NEW_VERSION ]; then
	echo $usage
	exit 1
fi

if [ -z $CONSUL_ADDR ]; then
	echo $usage
	exit 1
fi


Echo "Rolling out version $NEW_VERSION (sha=$GIT_COMMIT) of $APP using Consul $CONSUL_ADDR"

# Run the new service.
nohup java -jar $APP-$NEW_VERSION.jar

# Register the new service (auto?).

# Wait a bit, check if the new service has successfully registered.
echo "These are all deployed version"

http get http://$CONSUL_ADDR:8500/v1/catalog/service/$APP

# Deregister old service
