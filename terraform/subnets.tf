resource "aws_subnet" "private_a" {
  vpc_id            = var.vpc_id
  cidr_block        = "10.2.30.0/24"
  availability_zone = "us-west-2a"
}

resource "aws_subnet" "private_b" {
  vpc_id            = var.vpc_id
  cidr_block        = "10.2.31.0/24"
  availability_zone = "us-west-2b"
}

resource "aws_subnet" "private_db_a" {
  vpc_id            = var.vpc_id
  cidr_block        = "10.2.40.0/24"
  availability_zone = "us-west-2a"
}

resource "aws_subnet" "private_db_b" {
  vpc_id            = var.vpc_id
  cidr_block        = "10.2.41.0/24"
  availability_zone = "us-west-2b"
}


