#!/bin/bash

"/c/Program Files/Java/jdk1.8.0_112/bin/keytool" -genkey -v -alias dansirbu.myasustor.com \
-dname "CN=dansirbu.myasustor.com, OU=Dan Sirbu Home, L=Laval, ST=QC, C=CA, emailAddress=dansirbu101@yahoo.ca" \
-keypass home12auto34 -keystore serverX.jks -storepass home12auto34 -keyalg "RSA" -sigalg SHA512withRSA \
-keysize 2048 -validity 7300 

"/c/Program Files/Java/jdk1.8.0_112/bin/keytool" -importkeystore -srckeystore ~/serverX.jks -destkeystore ~/serverX.p12 -srcstoretype jks -deststoretype pkcs12

# get the certificate body
openssl pkcs12 -in ~/serverX.p12 -out ~/serverX.pem

#get the private key unecrypted
openssl pkcs12 -in ~/serverX.p12 -nodes -nocerts -out key.pem
