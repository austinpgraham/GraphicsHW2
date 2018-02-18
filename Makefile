build:
	gradle installDist

run:
	make build
	./build/install/template/bin/hw02