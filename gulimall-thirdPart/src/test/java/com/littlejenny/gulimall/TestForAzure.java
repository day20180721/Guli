package com.littlejenny.gulimall;

import com.azure.storage.blob.*;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.common.sas.SasProtocol;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

public class TestForAzure {
    public static void main(String[] args) {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        System.out.println(ft.format(new Date()));
//        two();
       }
    public static void one(){
        String connectString = "DefaultEndpointsProtocol=https;AccountName=gulimall;AccountKey=OCxGK05R3g18u5SYZ0jpuR7Xb1KX1/w+WMeb/SWvBFkZO6AfXD7kr2cGb06tokPQiseU0lwSXdPcw4Rzpd6iZg==;EndpointSuffix=core.windows.net";
        StorageSharedKeyCredential credential = StorageSharedKeyCredential.fromConnectionString(connectString);
        String sas = new BlobServiceSasSignatureValues()
                .setExpiryTime(OffsetDateTime.now().plusHours(1))
                .setPermissions(new BlobSasPermission().setReadPermission(true).setCreatePermission(true).setWritePermission(true))
                .setContainerName("brandicon")
                .setBlobName("gulimall")
                .generateSasQueryParameters(credential)
                .encode();
        System.out.println(sas);
    }
    public static void two(){
        String connectString = "DefaultEndpointsProtocol=https;AccountName=gulimall;AccountKey=OCxGK05R3g18u5SYZ0jpuR7Xb1KX1/w+WMeb/SWvBFkZO6AfXD7kr2cGb06tokPQiseU0lwSXdPcw4Rzpd6iZg==;EndpointSuffix=core.windows.net";
        BlobContainerSasPermission blobContainerSasPermission = new BlobContainerSasPermission()
                .setReadPermission(true)
                .setWritePermission(true)
                .setListPermission(true)
                .setCreatePermission(true);

        BlobServiceSasSignatureValues builder = new BlobServiceSasSignatureValues(
                OffsetDateTime.now().plusDays(1), blobContainerSasPermission)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        StorageSharedKeyCredential credential = new StorageSharedKeyCredential("gulimall","OCxGK05R3g18u5SYZ0jpuR7Xb1KX1/w+WMeb/SWvBFkZO6AfXD7kr2cGb06tokPQiseU0lwSXdPcw4Rzpd6iZg==");
        BlobClient client = new BlobClientBuilder()
                .connectionString(connectString)
                .blobName("gulimall")
                .credential(credential)
                .buildClient();
        System.out.println(String.format("https://%s.blob.core.windows.net/?%s",client.getAccountName(), client.generateSas(builder)));

    }

}

