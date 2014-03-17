# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.define :base do |base_config|
    base_config.vm.host_name = "base"
    base_config.vm.box = "precise64"
    base_config.vm.box_url = "http://files.vagrantup.com/precise64.box"
    base_config.vm.network :private_network, ip: "172.16.51.51"
    base_config.vm.network "forwarded_port", guest: 7199, host: 7199
    # cassandra accepts thrift client connections on this port
    base_config.vm.network "forwarded_port", guest: 9160, host: 9160

    base_config.vm.provider "virtualbox" do |vb|
      vb.customize ["modifyvm", :id, "--memory", "1024"]
    end

    base_config.vm.provision :puppet do |puppet|
      puppet.module_path = "modules"
      puppet.manifests_path = "manifests"
      puppet.manifest_file = "base.pp"
      puppet.options = "--verbose --debug"
    end
  end
end

