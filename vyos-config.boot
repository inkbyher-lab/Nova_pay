firewall {
    ipv4 {
        name ALLOW-ALL {
            default-action "accept"
        }
        name OPS-TO-IT {
            default-action "accept"
            rule 10 {
                action "drop"
                destination {
                    address "192.168.20.0/24"
                }
                source {
                    address "192.168.30.0/24"
                }
            }
        }
        name OPS-TO-MGMT {
            default-action "accept"
            rule 10 {
                action "drop"
                destination {
                    address "192.168.40.0/24"
                }
                source {
                    address "192.168.30.0/24"
                }
            }
        }
        name PAY-TO-IT {
            default-action "accept"
            rule 10 {
                action "drop"
                destination {
                    address "192.168.20.0/24"
                }
                source {
                    address "192.168.10.0/24"
                }
            }
        }
        name PAY-TO-MGMT {
            default-action "drop"
        }
        name PAY-TO-VPN {
            default-action "drop"
        }
    }
    zone IT {
        from MGMT {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from OPS {
            firewall {
                name "OPS-TO-IT"
            }
        }
        from PAYMENTS {
            firewall {
                name "PAY-TO-IT"
            }
        }
        from VPN {
            firewall {
                name "ALLOW-ALL"
            }
        }
        member {
            interface "eth1.20"
        }
    }
    zone MGMT {
        from IT {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from OPS {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from PAYMENTS {
            firewall {
                name "PAY-TO-MGMT"
            }
        }
        from VPN {
            firewall {
                name "ALLOW-ALL"
            }
        }
        member {
            interface "eth1.40"
        }
    }
    zone OPS {
        from IT {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from MGMT {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from PAYMENTS {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from VPN {
            firewall {
                name "ALLOW-ALL"
            }
        }
        member {
            interface "eth1.30"
        }
    }
    zone PAYMENTS {
        from MGMT {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from OPS {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from VPN {
            firewall {
                name "ALLOW-ALL"
            }
        }
        member {
            interface "eth1.10"
        }
    }
    zone VPN {
        from IT {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from MGMT {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from OPS {
            firewall {
                name "ALLOW-ALL"
            }
        }
        from PAYMENTS {
            firewall {
                name "PAY-TO-VPN"
            }
        }
        member {
            interface "vti0"
        }
    }
}
interfaces {
    ethernet eth0 {
        address "192.168.100.2/24"
        hw-id "0c:d8:36:ed:00:00"
    }
    ethernet eth1 {
        hw-id "0c:d8:36:ed:00:01"
        vif 10 {
            address "192.168.10.1/24"
        }
        vif 20 {
            address "192.168.20.1/24"
        }
        vif 30 {
            address "192.168.30.1/24"
        }
        vif 40 {
            address "192.168.40.1/24"
        }
    }
    loopback lo {
    }
    vti vti0 {
        address "169.254.49.189/30"
        description "AWS-VPN"
    }
}
nat {
    source {
        rule 100 {
            destination {
                address "10.2.0.0/16"
            }
            exclude
            outbound-interface {
                name "eth0"
            }
            source {
                address "192.168.0.0/16"
            }
        }
        rule 200 {
            outbound-interface {
                name "eth0"
            }
            source {
                address "192.168.0.0/16"
            }
            translation {
                address "masquerade"
            }
        }
    }
}
protocols {
    static {
        route 0.0.0.0/0 {
            next-hop 192.168.100.1 {
            }
        }
        route 10.2.0.0/16 {
            interface vti0 {
            }
        }
    }
}
service {
    dhcp-server {
        listen-interface "eth1.10"
        listen-interface "eth1.20"
        listen-interface "eth1.30"
        listen-interface "eth1.40"
        shared-network-name V10 {
            subnet 192.168.10.0/24 {
                option {
                    default-router "192.168.10.1"
                    name-server "192.168.20.10"
                }
                range 0 {
                    start "192.168.10.100"
                    stop "192.168.10.200"
                }
                subnet-id "10"
            }
        }
        shared-network-name V20 {
            subnet 192.168.20.0/24 {
                exclude "192.168.20.10"
                option {
                    default-router "192.168.20.1"
                    name-server "192.168.20.10"
                }
                range 0 {
                    start "192.168.20.100"
                    stop "192.168.20.200"
                }
                subnet-id "20"
            }
        }
        shared-network-name V30 {
            subnet 192.168.30.0/24 {
                option {
                    default-router "192.168.30.1"
                    name-server "192.168.20.10"
                }
                range 0 {
                    start "192.168.30.100"
                    stop "192.168.30.200"
                }
                subnet-id "30"
            }
        }
        shared-network-name V40 {
            subnet 192.168.40.0/24 {
                option {
                    default-router "192.168.40.1"
                    name-server "192.168.20.10"
                }
                range 0 {
                    start "192.168.40.100"
                    stop "192.168.40.200"
                }
                subnet-id "40"
            }
        }
    }
    ntp {
        allow-client {
            address "127.0.0.0/8"
            address "169.254.0.0/16"
            address "10.0.0.0/8"
            address "172.16.0.0/12"
            address "192.168.0.0/16"
            address "::1/128"
            address "fe80::/10"
            address "fc00::/7"
        }
        server time1.vyos.net {
        }
        server time2.vyos.net {
        }
        server time3.vyos.net {
        }
    }
    ssh {
        port "22"
    }
}
system {
    config-management {
        commit-revisions "100"
    }
    console {
        device ttyS0 {
            speed "115200"
        }
    }
    host-name "vyos"
    login {
        user vyos {
            authentication {
                encrypted-password "$6$rounds=656000$3SS2/bOrpljdh4rK$TNvrrUvzoDo6.vInFS3IExuUJiFHKs04DaP753UifqlYfTDSz90jhVpVZgQYfdTCKh7P7H4IPfcfKgpIuPNpe."
                plaintext-password ""
            }
        }
    }
    option {
        reboot-on-upgrade-failure "5"
    }
    syslog {
        local {
            facility all {
                level "info"
            }
            facility local7 {
                level "debug"
            }
        }
    }
}
vpn {
    ipsec {
        authentication {
            psk AWS {
                id "52.225.29.224"
                id "35.167.202.234"
                secret "DPnZT3zQ73pZAWBJGamUrZZ.twT.envv"
            }
        }
        esp-group ESP-AWS {
            lifetime "3600"
            pfs "dh-group2"
            proposal 1 {
                encryption "aes128"
                hash "sha1"
            }
        }
        ike-group IKE-AWS {
            dead-peer-detection {
                action "restart"
                interval "15"
                timeout "30"
            }
            lifetime "28800"
            proposal 1 {
                dh-group "2"
                encryption "aes128"
                hash "sha1"
            }
        }
        options {
        }
        site-to-site {
            peer AWS {
                authentication {
                    local-id "52.225.29.224"
                    mode "pre-shared-secret"
                }
                connection-type "initiate"
                force-udp-encapsulation
                ike-group "IKE-AWS"
                local-address "192.168.100.2"
                remote-address "35.167.202.234"
                vti {
                    bind "vti0"
                }
            }
        }
    }
}


// Warning: Do not remove the following line.
// vyos-config-version: "bgp@6:broadcast-relay@1:cluster@2:config-management@1:conntrack@6:conntrack-sync@2:container@3:dhcp-relay@2:dhcp-server@11:dhcpv6-server@6:dns-dynamic@4:dns-forwarding@4:firewall@19:flow-accounting@2:https@7:ids@2:interfaces@33:ipoe-server@4:ipsec@13:isis@3:l2tp@9:lldp@3:mdns@1:monitoring@2:nat@8:nat66@3:nhrp@1:ntp@3:openconnect@3:openvpn@4:ospf@2:pim@1:policy@9:pppoe-server@11:pptp@5:qos@3:quagga@12:reverse-proxy@3:rip@1:rpki@2:salt@1:snmp@3:ssh@2:sstp@6:system@29:vpp@1:vrf@3:vrrp@4:vyos-accel-ppp@2:wanloadbalance@4:webproxy@2"
// Release version: 2025.07.13-0023-rolling
