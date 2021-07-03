::start https://chromedriver.storage.googleapis.com/91.0.4472.101/chromedriver_win32.zip
:: https://superuser.com/questions/1314420/how-to-unzip-a-file-using-the-cmd/1406484
:: On Windows 10 build 17063 or later you can use tar.exe

if exist chromedriver.exe (
    echo chromedriver.exe is found in app directory
) else (
	echo chromedriver will be downloaded
	curl.exe --output chromedriver.zip --url https://chromedriver.storage.googleapis.com/91.0.4472.101/chromedriver_win32.zip
	:: curl.exe --output chromedriver.zip --url https://chromedriver.storage.googleapis.com/92.0.4515.43/chromedriver_win32.zip
	tar -xf chromedriver.zip
	del chromedriver.zip
)

:: Remove Old Build files
rd target /s /q

:: Building the program
mvn clean package

PAUSE