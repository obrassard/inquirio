package ca.obrassard.inquirio;

import ca.obrassard.inquirio.transfer.ReportType;

class ReportUserRequest {
    public long reportID;
    public long notificationID;
    public ReportType category;
    public String detail;
}
