/**
 * Copyright (c) 2015-2016, President and Fellows of Harvard College
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.harvard.catalyst.scheduler.dto;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.harvard.catalyst.scheduler.util.MiscUtil.fullName;
import static edu.harvard.catalyst.scheduler.util.MiscUtil.q;
import static edu.harvard.catalyst.scheduler.util.MiscUtil.showDateTime;

public class DailyAdmReportDTO implements CsvAbleDTO {

  protected Integer resourceTypeId;
  protected String resourceTypeName;
  protected Integer visitId;
  private Integer subjectId;
  private Integer visitTempId;
  private String subjectFirstName;
  private String subjectMiddleName;
  private String subjectLastName;
  private String birthdate;
  private String mrn;
  private String genderName;
  private String localId;
  private String irb;
  private String visitName;
  private String visitStatus;
  private String resourceName;
  private Date checkInTime;
  private Date checkOutTime;
  private Date scheduledStartTime;
  private Date scheduledEndTime;
  private String comment;
  private String scheduledVisitComment;

  private String schedulingFlavor;

  public DailyAdmReportDTO() {
  }

  public Integer getSubjectId() {
    return subjectId;
  }
  public void setSubjectId(Integer subjectId) {
    this.subjectId = subjectId;
  }
  
  public String getSubjectFirstName() {
    return subjectFirstName;
  }
  public void setSubjectFirstName(String subjectFirstName) {
    this.subjectFirstName =   subjectFirstName;
  }
  
  public String getSubjectMiddleName() {
    return subjectMiddleName;
  }
  public void setSubjectMiddleName(String subjectMiddleName) {
    this.subjectMiddleName = subjectMiddleName;
  }
  
  public String getSubjectLastName() {
    return subjectLastName;
  }
  public void setSubjectLastName(String subjectLastName) {
    this.subjectLastName = subjectLastName;
  }
  
  public String getMrn() {
    return mrn;
  }
  public void setMrn(String mrn) {
    this.mrn = mrn;
  }
  
  public String getGenderName() {
    return genderName;
  }
  public void setGenderName(String genderName) {
    this.genderName = genderName;
  }
  
  public String getLocalId() {
    return localId;
  }
  public void setLocalId(String localId) {
    this.localId = localId;
  }
  
  public String getIrb() {
    return irb;
  }
  public void setIrb(String irb) {
    this.irb = irb;
  }
  
  public String getVisitName() {
    return visitName;
  }
  public void setVisitName(String visitName) {
    this.visitName = visitName;
  }
  
  public String getResourceName() {
    return resourceName;
  }
  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }
  
  public Date getCheckInTime() {
    return checkInTime;
  }
  public void setCheckInTime(Date checkInTime) {
    this.checkInTime = checkInTime;
  }
  
  public Date getCheckOutTime() {
    return checkOutTime;
  }
  public void setCheckOutTime(Date checkOutTime) {
    this.checkOutTime = checkOutTime;
  }
  
  public Date getScheduledStartTime() {
    return scheduledStartTime;
  }
  public void setScheduledStartTime(Date scheduledStartTime) {
    this.scheduledStartTime = scheduledStartTime;
  }
  
  public Date getScheduledEndTime() {
    return scheduledEndTime;
  }
  public String getComment() {
	return comment;
}

public void setComment(String comment) {
	this.comment = comment;
}


  public String getScheduledVisitComment() {
    return scheduledVisitComment;
  }

  public void setScheduledVisitComment(String scheduledVisitComment) {
    this.scheduledVisitComment = scheduledVisitComment;
  }

  public String getSchedulingFlavor(){ return schedulingFlavor;}

  public void setSchedulingFlavor(String schedulingFlavor) {
    this.schedulingFlavor = schedulingFlavor;
  }

public void setScheduledEndTime(Date scheduledEndTime) {
    this.scheduledEndTime = scheduledEndTime;
  }

  public String getBirthdate() {
    return birthdate;
  }
  public void setBirthdate(String birthdate) {
    this.birthdate = birthdate;
  }

  public Integer getVisitTempId() {
    return visitTempId;
  }

  public void setVisitTempId(Integer visitTempId) {
    this.visitTempId = visitTempId;
  }

public String getVisitStatus() {
    return visitStatus;
}

public void setVisitStatus(String visitStatus) {
    this.visitStatus = visitStatus;
}

  public Integer getVisitId() {
    return visitId;
  }

  public void setVisitId(Integer visitId) {
    this.visitId = visitId;
  }
  public Integer getResourceTypeId() {
    return resourceTypeId;
  }

  public void setResourceTypeId(Integer resourceTypeId) {
    this.resourceTypeId = resourceTypeId;
  }

  public String getResourceTypeName() {
    return resourceTypeName;
  }

  public void setResourceTypeName(String resourceTypeName) {
    this.resourceTypeName = resourceTypeName;
  }



  public String[] getCommentedRow(String[] row, String comment, String scheduledVisitComment) {
    if (scheduledVisitComment.equals("None")) {
      row[19] += q(comment + " ");
    }

    if (scheduledVisitComment.equals("Nutrition")) {
      row[12] += q(comment + "\n");
    } else if (scheduledVisitComment.equals("EBL")) {
      row[13] += q(comment + "\n");
    } else if (scheduledVisitComment.equals("Nursing")) {
      row[14] += q(comment + "\n");
    } else if (scheduledVisitComment.equals("Cardiovascular Imaging")) {
      row[15] += q(comment + "\n");
    } else if (scheduledVisitComment.equals("Lab")) {
      row[16] += q(comment + "\n");
    } else if (scheduledVisitComment.equals("Pharmacy")) {
      row[17] += q(comment + "\n");
    } else if (scheduledVisitComment.equals("Other")) {
      row[18] += q(comment + "\n");
    }
    return row;
  }




  @Override
  public String toCsvHeaders() {
    return "Subject Name,DOB,MRN,Gender,Local ID,IRB #,Visit Name," +
            "Visit Status, Check In Time,Resource Name," +
            "Resource Start Time,Resource End Time, Nutrition, EBL, Nursing, Cardiovascular Imaging, Lab, Pharmacy, Other, None";
  }




  @Override
  public List<String> toCsvRows(List<?> dtoList) {
    List<String> result = Lists.newArrayList();
    result.add(toCsvHeaders() + "\n");

    Map<String, String[]> rowMap = new HashMap<>();

    for (Object object: dtoList) {
      DailyAdmReportDTO d = (DailyAdmReportDTO) object;

      List<String> columns = Lists.newArrayList();

      String rowMapKey = d.visitId + d.resourceName + d.resourceTypeId;

      if (rowMap.containsKey(rowMapKey)) {
        String[] newRow = getCommentedRow(rowMap.get(rowMapKey), d.comment, d.scheduledVisitComment);
        rowMap.put(rowMapKey, newRow);

      } else {

        String[] row = new String[21];
        row = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",""};
        row[0] = q(fullName(
                d.subjectFirstName,
                d.subjectMiddleName,
                d.subjectLastName));
        row[1] = q(d.birthdate);
        row[2] = q(d.mrn);
        row[3] = q(d.genderName);
        row[4] = q(d.localId);
        row[5] = q(d.irb);
        row[6] = q(d.visitName);
        row[7] = q(d.visitStatus);
        row[8] = q(showDateTime(d.checkInTime));
        row[9] = q(d.resourceName);
        row[10] = q(showDateTime(d.scheduledStartTime));
        row[11] = q(showDateTime(d.scheduledEndTime));
        String[] newRow = getCommentedRow(row, d.comment, d.scheduledVisitComment);
        rowMap.put(rowMapKey, newRow);

      }
    }

    for (Map.Entry<String, String[]> entry : rowMap.entrySet()) {
      String rows = Joiner.on(",").join(entry.getValue());
      result.add(rows + "\n");
    }
    return result;
  }
}
