package ru.simbirsoft.test;

import org.junit.jupiter.api.AfterEach;
import ru.simbirsoft.requests.YandexDiskRequests;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;

public class YandexDiskBaseTest {
    protected final YandexDiskRequests yandexDiskRequests  = new YandexDiskRequests();

    private final List<String> createdFolders = new ArrayList<>();

    @AfterEach
    void cleanUpYandexDiskFolders(){
        createdFolders.forEach(this::deleteFolderExists);
        createdFolders.forEach(this::deleteTrashFolderExists);
        createdFolders.clear();
    }

    protected void addFolderToCleanUp(String path) {
        createdFolders.add(path);
    }

    protected void createFolderAndAddCleanUp(String path){
        yandexDiskRequests.createFolder(path)
                .then()
                .statusCode(HTTP_CREATED);

        addFolderToCleanUp(path);
    }

    private void deleteFolderExists(String path){
        yandexDiskRequests.deleteFolder(path);
    }

    private void deleteTrashFolderExists(String path){
        yandexDiskRequests.deleteTrashFolder(path);
    }
}
