resource "aws_key_pair" "kashflow_key" {
  key_name   = var.key_pair_name
  public_key = file("~/.ssh/id_rsa.pub")
}

resource "aws_security_group" "kashflow_sg" {
  name        = "kashflow-sg"
  description = "Allow SSH, HTTP and Kafka ports"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTP"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Kafka"
    from_port   = 9092
    to_port     = 9092
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }
}

data "aws_vpc" "default" {
  default = true
}

resource "aws_instance" "kashflow_app" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = var.instance_type
  key_name      = aws_key_pair.kashflow_key.key_name
  security_groups = [aws_security_group.kashflow_sg.name]

  tags = {
    Name = "kashflow-app"
  }

  user_data = <<-EOF
              #!/bin/bash
              # Redireciona a saída para um log
              exec > /home/ubuntu/startup.log 2>&1

              # Atualiza pacotes
              apt update -y
              apt install -y git docker.io docker-compose

              # Adiciona usuário ubuntu ao grupo docker
              usermod -aG docker ubuntu

              # Clona o repositório
              cd /home/ubuntu
              git clone https://github.com/franciscoaraujo/kashflow.git

              # Sobe os containers
              cd /home/ubuntu/kashflow
              docker-compose up -d
              EOF

}
