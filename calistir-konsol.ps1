chcp 65001 | Out-Null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::InputEncoding = [System.Text.Encoding]::UTF8
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=console" "-Dfile.encoding=UTF-8"