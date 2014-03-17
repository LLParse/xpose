class cassandra(

        $cassandra_archive = "apache-cassandra-2.0.6-bin.tar.gz",
        $cassandra_version = "2.0.6"
        ) 
        {

    Exec {
        path => [ "/usr/bin", "/bin", "/usr/sbin"]
    }   

        file { ["/opt/apache","/var/lib/cassandra","/var/log/cassandra","/var/lib/cassandra/saved_caches"]:
                owner  => 'vagrant',
                group  => 'vagrant',
                ensure => 'directory',
                mode   => 755,
        }

        exec { 'get cassandra' :
        cwd     => '/tmp',
        command => "wget http://www.apache.org/dist/cassandra/${cassandra_version}/${cassandra_archive}",
    }

        exec { 'extract cassandra':
        cwd     => '/opt/apache',
        command => "tar xfv /tmp/${cassandra_archive}",
        require => Exec['get cassandra'],
    }    

    exec { 'update cassandra yaml' :
      cwd     => '/tmp',
      command => "cp /vagrant/vagrant/cassandra.yaml /opt/apache/apache-cassandra-2.0.3/conf",
      require => Exec['extract cassandra'],
    }    

        exec { 'start cassandra':
        cwd     => '/tmp',
        command => "/opt/apache/apache-cassandra-${cassandra_version}/bin/cassandra",
        require => Exec['extract cassandra'],
    }    
}

