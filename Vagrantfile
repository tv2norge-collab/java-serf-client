# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  # All Vagrant configuration is done here. The most common configuration
  # options are documented and commented below. For a complete reference,
  # please see the online documentation at vagrantup.com.

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box = "ubuntu/trusty64"

  config.vm.provision "file", source: "serf-agent.conf", destination: "./serf-agent.conf"
  config.vm.provision "shell", path: "provision.sh"

  config.vm.define "serf1" do |serf1|
    serf1.vm.hostname = "serf1"
    serf1.vm.network "private_network", ip: "10.2.2.10"
    serf1.vm.network "forwarded_port", guest: 7373, host: 7373
  end

  config.vm.define "serf2" do |serf2|
    serf2.vm.hostname = "serf2"
    serf2.vm.network "private_network", ip: "10.2.2.11"
    serf2.vm.network "forwarded_port", guest: 7373, host: 7374
  end
end
