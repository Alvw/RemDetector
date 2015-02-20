package dreamrec;

import java.io.File;

/**
 * Created by mac on 17/02/15.
 */
public interface InputEventHandler {
    public void startRecording(RecordingSettings recordingSettings, File file) throws ApplicationException;
    public void stopRecording()throws ApplicationException;
    public RecordingSettings getRecordingSettings(File file) throws ApplicationException;
    public String normalizeFilename(String filename);
    public String[] getFileExtensions();
}
