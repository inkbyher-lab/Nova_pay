data "aws_internet_gateway" "existing" {
  filter {
    name   = "attachment.vpc-id"
    values = ["vpc-02bc2473cf5d00d4b"]
  }
}

resource "aws_eip" "nat_eip" {
  domain = "vpc"
}

resource "aws_nat_gateway" "nat" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = var.public_subnet_1

  lifecycle {
    ignore_changes = [tags, tags_all]
  }
}

resource "aws_route_table" "public_rt" {
  vpc_id = var.vpc_id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = data.aws_internet_gateway.existing.id
  }
}

resource "aws_route_table" "private_rt" {
  vpc_id = var.vpc_id
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat.id
  }
}

resource "aws_route_table_association" "private_a" {
  subnet_id      = aws_subnet.private_a.id
  route_table_id = aws_route_table.private_rt.id
}

resource "aws_route_table_association" "private_b" {
  subnet_id      = aws_subnet.private_b.id
  route_table_id = aws_route_table.private_rt.id
}

resource "aws_route_table" "db_rt" {
  vpc_id = var.vpc_id
}

resource "aws_route_table_association" "db_a" {
  subnet_id      = aws_subnet.private_db_a.id
  route_table_id = aws_route_table.db_rt.id
}

resource "aws_route_table_association" "db_b" {
  subnet_id      = aws_subnet.private_db_b.id
  route_table_id = aws_route_table.db_rt.id
}


resource "aws_customer_gateway" "onprem" {
  bgp_asn    = 65000
  ip_address = "168.62.223.165"
  type       = "ipsec.1"

  lifecycle {
    ignore_changes = [tags, tags_all]
  }
}

resource "aws_vpn_gateway" "vgw" {
  vpc_id = var.vpc_id

  lifecycle {
    ignore_changes = [tags, tags_all]
  }
}

resource "aws_vpn_connection" "site_to_site" {
  vpn_gateway_id      = aws_vpn_gateway.vgw.id
  customer_gateway_id = aws_customer_gateway.onprem.id
  type                = "ipsec.1"
  static_routes_only  = true

  lifecycle {
    ignore_changes = [tags, tags_all]
  }
}

resource "aws_vpn_connection_route" "onprem" {
  vpn_connection_id      = aws_vpn_connection.site_to_site.id
  destination_cidr_block = "192.168.0.0/16"
}

resource "aws_vpn_gateway_route_propagation" "private" {
  vpn_gateway_id = aws_vpn_gateway.vgw.id
  route_table_id = aws_route_table.private_rt.id
}


