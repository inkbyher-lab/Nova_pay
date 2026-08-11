data "aws_ami" "al2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}



resource "aws_instance" "app_a" {
  ami                    = data.aws_ami.al2023.id
  instance_type          = "t3.micro"
  subnet_id              = aws_subnet.private_a.id
  vpc_security_group_ids = [aws_security_group.ec2_sg.id, aws_security_group.vpn_sg.id]
  key_name               = var.key_name

  credit_specification {
    cpu_credits = "standard"
  }

  lifecycle {
    ignore_changes = [tags, tags_all]
  }
}

resource "aws_instance" "app_b" {
  ami                    = data.aws_ami.al2023.id
  instance_type          = "t3.micro"
  subnet_id              = aws_subnet.private_b.id
  vpc_security_group_ids = [aws_security_group.ec2_sg.id, aws_security_group.vpn_sg.id]
  key_name               = var.key_name

  credit_specification {
    cpu_credits = "standard"
  }

  lifecycle {
    ignore_changes = [tags, tags_all]
  }
}

resource "aws_instance" "nginx" {
  ami                         = data.aws_ami.al2023.id
  instance_type               = "t3.micro"
  subnet_id                   = var.public_subnet_1
  vpc_security_group_ids      = [aws_security_group.alb_sg.id, aws_security_group.vpn_sg.id]
  key_name                    = var.key_name
  associate_public_ip_address = true

  credit_specification {
    cpu_credits = "standard"
  }

  lifecycle {
    ignore_changes = [tags, tags_all]
  }
}

resource "aws_instance" "db" {
  ami                    = data.aws_ami.al2023.id
  instance_type          = "t3.micro"
  subnet_id              = aws_subnet.private_db_a.id
  vpc_security_group_ids = [aws_security_group.rds_sg.id, aws_security_group.vpn_sg.id]
  key_name               = var.key_name

  credit_specification {
    cpu_credits = "standard"
  }

  lifecycle {
    ignore_changes = [tags, tags_all]
  }
}


