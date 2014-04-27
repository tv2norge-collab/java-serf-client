wget -q https://dl.bintray.com/mitchellh/serf/0.6.0_linux_amd64.zip
sudo apt-get -yq install unzip
unzip 0.6.0_linux_amd64.zip
sudo mv serf /usr/local/bin/
sudo cp serf-agent.conf /etc/init/
sudo start serf-agent
