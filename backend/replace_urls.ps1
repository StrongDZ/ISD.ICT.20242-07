$content = Get-Content 'src/main/resources/data.sql' -Raw
$newContent = $content -replace 'https://[^'']+\.jpg', 'https://vi.wikipedia.org/wiki/Th%E1%BB%A7y_h%E1%BB%AD_%28phim_truy%E1%BB%81n_h%C3%ACnh_2011%29#/media/T%E1%BA%ADp_tin:Th%E1%BB%A7y_h%E1%BB%AD_2011.jpg'
Set-Content 'src/main/resources/data.sql' -Value $newContent
Write-Host "URL replacement completed successfully!" 