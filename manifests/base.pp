group { "puppet":
  ensure => "present",
}

File { owner => 0, group => 0, mode => 0644 }

file { '/etc/motd':
  content => "
X-POSE v0.1 Alpha
Author: James Oliver
"
}

class base {
  exec { 'apt-get update':
   command => '/usr/bin/apt-get update'
  }
}

include base
include java
include cassandra
Class['base'] -> Class['java']
Class['java'] -> Class['cassandra']
