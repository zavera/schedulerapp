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
import scala.Int;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.harvard.catalyst.scheduler.util.MiscUtil.*;

public class NutritionDailyResourceReportDTO extends DailyResourceReportDTO {

  public NutritionDailyResourceReportDTO(final DailyResourceReportDTO uncle) {
    resourceTypeId = uncle.resourceTypeId;
    resourceTypeName = uncle.resourceTypeName;
    resourceName = uncle.resourceName;
    scheduledStartTime = uncle.scheduledStartTime;
    scheduledEndTime = uncle.scheduledEndTime;
    localId = uncle.localId;
    irb = uncle.irb;
    visitName = uncle.visitName;
    visitTypeName = uncle.visitTypeName;
    subjectFirstName = uncle.subjectFirstName;
    subjectMiddleName = uncle.subjectMiddleName;
    subjectLastName = uncle.subjectLastName;
    mrn = uncle.mrn;
    comment = uncle.comment;
    visitId = uncle.visitId;
    scheduledVisitComment = uncle.scheduledVisitComment;
    schedulingFlavor = uncle.schedulingFlavor;
  }


  public String[] getCommentedRow(String[] row, String comment, String scheduledVisitComment,String schedulingFlavor){
      if(scheduledVisitComment.equals("None")){
          row[15] += q(comment+" ");
      }
      if(schedulingFlavor.equals("Overbooked")) {
          if (scheduledVisitComment.equals("Nutrition")) {
              row[8] += q(comment+" ");
          } else if (scheduledVisitComment.equals("EBL")) {
              row[9] += q(comment+" ");
          } else if (scheduledVisitComment.equals("Nursing")) {
              row[10] +=  q(comment+" ");
          } else if (scheduledVisitComment.equals("Cardiovascular Imaging")) {
              row[11] +=  q(comment+" ");
          } else if (scheduledVisitComment.equals("Lab")) {
              row[12] +=  q(comment+" ");
          } else if (scheduledVisitComment.equals("Pharmacy")) {
              row[13] +=  q(comment+" ");
          } else if (scheduledVisitComment.equals("Other")) {
              row[14] +=  q(comment+" ");
          }
      }
      if(schedulingFlavor.equals("Scheduled")){
          if (scheduledVisitComment.equals("Nutrition")) {
              row[8] =  q(comment);
          } else if (scheduledVisitComment.equals("EBL")) {
              row[9] =  q(comment);
          } else if (scheduledVisitComment.equals("Nursing")) {
              row[10] =  q(comment);
          } else if (scheduledVisitComment.equals("Cardiovascular Imaging")) {
              row[11] =  q(comment);
          } else if (scheduledVisitComment.equals("Lab")) {
              row[12] =  q(comment);
          } else if (scheduledVisitComment.equals("Pharmacy")) {
              row[13] =  q(comment);
          } else if (scheduledVisitComment.equals("Other")) {
              row[14] =  q(comment);
          }

      }
      return row;

  }

  @Override
  public String toCsvHeaders() {
    return "Resource Name,Subject Name,MRN,Visit ID,Visit Name," +
            "Resource Start Time,Resource End Time,Resource Usage Duration," +
            "Nutrition, EBL, Nursing, Cardiovascular Imaging, Lab, Pharmacy, Other, None";
  }

  @Override
  public List<String> toCsvRows(List<?> dtoList) {

      List<String> result = Lists.newArrayList();
      result.add(toCsvHeaders() + "\n");

      Map<Integer, String[]> rowMap = new HashMap<>();
      for (Object object : dtoList) {
          NutritionDailyResourceReportDTO d = (NutritionDailyResourceReportDTO) object;

          if (rowMap.containsKey(d.visitId)) {

              String[] newRow = getCommentedRow(rowMap.get(d.visitId), d.comment, d.scheduledVisitComment,d.schedulingFlavor);
              rowMap.put(d.visitId, newRow);
          } else {
              String[] row = new String[16];
              row = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
              row[0] = q(d.resourceName);
              row[1] = q(fullName(
                      d.subjectFirstName,
                      d.subjectMiddleName,
                      d.subjectLastName));
              row[2] = q(d.mrn);
              row[3] = q(d.visitId);
              row[4] = q(d.visitName);
              row[5] = q(showDateTime(d.scheduledStartTime));
              row[6] = q(showDateTime(d.scheduledEndTime));
              row[7] = q(formatEndMinusStart(
                      d.scheduledStartTime, d.scheduledEndTime));
              String[] newRow = getCommentedRow(row, d.comment, d.scheduledVisitComment,d.schedulingFlavor);
              rowMap.put(d.visitId, newRow);
          }
      }

      for (Map.Entry<Integer,String[]> entry : rowMap.entrySet()) {
          String rows = Joiner.on(",").join(entry.getValue());
          result.add(rows + "\n");
      }

      return result;
  }
}
