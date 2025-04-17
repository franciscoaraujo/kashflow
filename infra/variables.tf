variable "aws_region" {
  default = "us-east-1"
}

variable "aws_profile" {
  default = "btw.francisco.admin-ec2" # seu profile do AWS CLI
}

variable "key_pair_name" {
  default = "kashflow-key"
}

variable "instance_type" {
  default = "t2.micro"
}
