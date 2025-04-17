output "instance_public_ip" {
  value = aws_instance.kashflow_app.public_ip
}

output "access_url" {
  value = "http://${aws_instance.kashflow_app.public_ip}:8080"
}
