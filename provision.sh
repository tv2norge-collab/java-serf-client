wget -q https://releases.hashicorp.com/serf/0.8.1/serf_0.8.1_linux_amd64.zip
sudo apt-get -yq install unzip
unzip serf_0.8.1_linux_amd64.zip
sudo mv serf /usr/local/bin/
sudo cp serf-agent.conf /etc/init/
sudo start serf-agent
