# Novapay Hybrid Cloud Network Project

## Repository File Structure

* **`GNS3 Topology.png`**: Visual diagram of the on-premises GNS3 topology.
* **`Novapay_Functionality_Report.docx`**: Graded functionality report demonstrating the operation and verification of the environment.
* **`Onprem_net.gns3`**: GNS3 project file to spin up the complete on-premises network simulation.
* **`/terraform`**: Directory containing Terraform configuration files used to provision the AWS cloud environment.
* **`Vyos-config.boot`**: Configuration file for the VyOS router.
* **`README.md`**: Project overview including scenario, scope, diagram, test cases, constraints, and retrospective.


## Organizational Scenario Overview

**Novapay** is a mid-market fintech company providing payment processing infrastructure to both merchants (B2B) and consumers (B2C). The company operates a merchant-facing API platform that enables businesses to accept and settle payments, alongside a consumer-facing application that allows end users to manage transactions and transfer funds. As transaction volumes scale, Novapay requires a network architecture that prioritizes performance under load, resilience against failure, and rapid recovery in disaster scenarios.

The organizational need driving this project is the gap between Novapay’s existing on-premises infrastructure and the elasticity demands of a growing payments platform. A hybrid architecture connecting a structured on-premises office network to AWS-hosted application and database tiers addresses this gap by keeping internal operations on-premises while offloading scalable, customer-facing workloads to the cloud. This design directly supports Novapay’s core priorities of performance while scaling, resilience, and disaster recovery.


## Network Scope

Novapay operates a hybrid network environment connecting a simulated on-premises office network in GNS3 to a three-tier AWS cloud infrastructure over a Site-to-Site IPSec VPN. 

### On-Premises Network (GNS3)
The on-premises environment consists of a single router and two Layer 3 switches supporting four department VLANs across a 10-device network using the `192.168.0.0/16` address space:
* **VLAN 10**: Payments
* **VLAN 20**: IT
* **VLAN 30**: Operations
* **VLAN 40**: Management

A DHCP server in the IT VLAN provides dynamic addressing to all workstations via DHCP Relay, while Access Control Lists (ACLs) on the router enforce controlled inter-VLAN access, and NAT provides internet connectivity for on-premises users.

### Cloud Network (AWS)
The AWS side is built within a single VPC (`10.0.0.0/16`) in `us-east-1`, segmented into public and private subnets spanning two Availability Zones. 
* An internet-facing Application Load Balancer occupies the public subnets.
* EC2 application instances reside in the private app subnets.
* An RDS database tier is isolated in dedicated private database subnets.
* Security groups enforce strict traffic boundaries between each tier.

### Hybrid Connectivity
A Virtual Private Gateway on the AWS side and a Customer Gateway on the on-premises router establish an IPSec tunnel, enabling encrypted communication between the `192.168.0.0/16` on-premises network and the `10.0.0.0/16` VPC. All AWS infrastructure is defined and deployed via Terraform for consistent, repeatable provisioning.


## Network Diagram
(in the files, Network Diagram.jpeg)

## Predefined Test Cases

| Test Case | Category | Requirement Description |
| :--- | :--- | :--- |
| **Test Case #1** | **Local Networks** — Basic Network Segmentation at Layer 2 via VLANs and 802.1q | Network traffic must be segmented per department/service function at Layer 2 to enhance security and reduce switching-layer congestion while allowing segmented traffic to traverse between switches via VLAN trunking. Configuration choices and isolation verification must be documented. |
| **Test Case #2** | **Local Networks** — Accessing External Resources: Routing and Traffic Security | User devices must receive dynamic IP addresses via DHCP unless hosting a service requiring a static address. At least one network resource must be assigned a static address. |
| **Test Case #3** | **Local Networks** — Device Discovery and Reachability | Include multiple network segments with access controls allowing specific inter-segment communication while blocking unauthorized inter-segment traffic using ACLs. |
| **Test Case #4** | **Cloud Networks** — Network Segmentation | Cloud traffic within the AWS VPC must be segmented using subnets and route tables. Platform-specific security components must enforce network isolation across tiers. |
| **Test Case #5** | **Cloud Networks** — Accessing External Resources: Routing and Traffic Security | Cloud resources should typically utilize dynamic IP assignment from the platform, with at least one resource assigned a static IP address. |
| **Test Case #6** | **Cloud Networks** — Device Discovery and Reachability | Enforce strict access control rules within the VPC using Security Groups/NACLs, permitting required inter-tier traffic while denying all non-essential traffic. |
| **Test Case #7** | **VPN** — Bridging the Networks *(Custom)* | Verify IPSec Site-to-Site VPN connectivity between the on-premises GNS3 network and the AWS VPC. Ensure bi-directional encrypted communication across private ranges. |
| **Test Case #8** | **Hybrid Network Security** *(Custom)* | Verify cross-boundary security controls, ensuring on-premises ACLs and AWS Security Groups enforce consistent access policies for transit traffic. |


## Technical Constraints & Environment Limitations

* **AWS Resource Caps:** Restricted to a maximum of 10 EC2 instances alongside standard security groups, Network ACLs (NACLs), and route tables. Managed services like Application Load Balancers (ALBs) and RDS were off-limits during final deployment.
* **IAM Restrictions:** Lacked permissions to modify IAM roles, policies, or user access control.
* **Time Sensitivity:** The lab operated on a strict, non-pausable 8-hour timer without progress saving. Fast automated redeployment was required to survive timer expiration.
* **Tooling Challenges:** Encountered versioning issues with VyOS, along with performance bottlenecks and limited appliance options within GNS3.


## Project Gaps & Lessons Learned

Due to the strict resource constraints, several production-grade networking and security controls could not be implemented in this iteration:
* Custom IAM policies demonstrating the Principle of Least Privilege (PoLP).
* CloudFront CDN and Route 53 DNS routing.
* AWS WAF and dedicated perimeter security appliances.
* Managed RDS, ALBs, AWS Systems Manager (SSM), and dedicated deployment pipelines.


## Key Takeaways & Current Workflow

Despite these limitations, the project provided extensive hands-on experience in low-level hybrid networking and infrastructure automation. Following local hypervisor, NAT, and KVM issues while trying to recreate this setup locally, future testing and network emulation workflows have been pivoted to **Containerlab** and full cloud-native environments.

Author: Munira Ahmed | Cloud/Platform/DevOps Engineer
Certifications: A+, Net+, Sec+, Linux Essentials, ITIL4, AWS CCP, AWS SAA | AWS SOA-C03
