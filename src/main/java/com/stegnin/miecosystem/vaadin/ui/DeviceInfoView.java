package com.stegnin.miecosystem.vaadin.ui;

import com.stegnin.miecosystem.model.Device;
import com.stegnin.miecosystem.model.DeviceInfo;
import com.stegnin.miecosystem.service.ExtractDeviceInfoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route("device/info")
@Theme(value = Material.class, variant = Material.LIGHT)
@PageTitle("ИНФОРМАЦИЯ ОБ УСТРОЙСТВЕ")
public class DeviceInfoView extends VerticalLayout {

    private MemoryBuffer buffer;
    private Upload upload;
    private Button download;
    private final ExtractDeviceInfoService deviceInfoService;
    private Grid<DeviceInfo> grid;

    public DeviceInfoView(ExtractDeviceInfoService deviceInfoService) {
        this.deviceInfoService = deviceInfoService;
        this.buffer = new MemoryBuffer();
        this.download = new Button("ПОЛУЧИТЬ ИНФОРМАЦИЮ", e -> getDeviceInfo());
        this.grid = new Grid<>(); // инициализация Grid'a
        init();
    }

    private void getDeviceInfo() {
        grid.setItems(deviceInfoService.extractDeviceInfo(buffer));
        add(grid);
    }

    private void init() {
        download.setEnabled(false);
        grid.addColumn(Device::getIp)
                .setHeader("IP")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        grid.addColumn(Device::getMac)
                .setHeader("MAC")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        grid.addColumn(Device::getModel)
                .setHeader("МОДЕЛЬ")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        grid.addColumn(Device::getName)
                .setHeader("НАЗВАНИЕ")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        grid.addColumn(Device::getToken)
                .setHeader("ТОКЕН")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        upload = initUpload();
        add(upload, download);
        setAlignItems(Alignment.CENTER);
    }

    private Upload initUpload() {
        Upload upload = new Upload(buffer);
        upload.setId("i18n-upload");
        UploadI18N i18n = new UploadI18N();
        i18n.setDropFiles(
                new UploadI18N.DropFiles()
                        .setOne("ИЛИ ПЕРЕТАЩИ ЕГО СЮДА...")
                        .setMany("ИЛИ ПЕРЕТАЩИ ИХ СЮДА..."))
                .setAddFiles(new UploadI18N.AddFiles()
                        .setOne("ВЫБЕРИ ФАЙЛ")
                        .setMany("ДОБАВЬ ФАЙЛ"))
                .setCancel("ОТМЕНИТЬ")
                .setError(new UploadI18N.Error()
                        .setTooManyFiles("СЛИШКОМ МНОГО ФАЙЛОВ.")
                        .setFileIsTooBig("СЛИШКОМ БОЛЬШОЙ ФАЙЛ.")
                        .setIncorrectFileType("НЕКОРРЕКТНЫЙ ТИП ФАЙЛА."))
                .setUploading(new UploadI18N.Uploading()
                        .setStatus(new UploadI18N.Uploading.Status()
                                .setConnecting("СОЕДИНЕНИЕ...")
                                .setStalled("ЗАГРУЗКА ЗАСТОПОРИЛАСЬ.")
                                .setProcessing("ОБРАБОТКА ФАЙЛА..."))
                        .setRemainingTime(
                                new UploadI18N.Uploading.RemainingTime()
                                        .setPrefix("ОСТАВШЕЕСЯ ВРЕМЯ: ")
                                        .setUnknown(
                                                "ОСТАВШЕЕСЯ ВРЕМЯ НЕИЗВЕСТНО"))
                        .setError(new UploadI18N.Uploading.Error()
                                .setServerUnavailable("СЕРВЕР НЕДОСТУПЕН")
                                .setUnexpectedServerError(
                                        "НЕОЖИДАННАЯ ОШИБКА СЕРВЕРА")
                                .setForbidden("ЗАГРУЗКА ЗАПРЕЩЕНА")))
                .setUnits(Stream
                        .of("Б", "КБАЙТ", "МБАЙТ", "ГБАЙТ", "ТБАЙТ", "ПБАЙТ",
                                "ЭБАЙТ", "ЗБАЙТ", "ИБАЙТ")
                        .collect(Collectors.toList()));

        upload.setI18n(i18n);
        upload.setAcceptedFileTypes(".sqlite");
        upload.addSucceededListener(e -> download.setEnabled(true));
        return upload;
    }

}
