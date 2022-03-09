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
package edu.harvard.catalyst.scheduler.service;

import static edu.harvard.catalyst.hccrc.core.util.ListUtils.enrich;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import edu.harvard.catalyst.scheduler.entity.VisitType;
import edu.harvard.catalyst.scheduler.persistence.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class AppServiceTest {
    @Mock
    private ResourceDAO resourceDao;

    @Mock
    private StudyDAO studyDao;

    @Mock
    private SubjectDAO subjectDao;

    @Mock
    private AuthDAO authDao;


    @Mock
    private AppointmentDAO appointmentDAO;
    
    @Test
    public void testVisitTypesFromGetStaticListsMap() {
        final AppService service = new AppService(studyDao, resourceDao, subjectDao, authDao,appointmentDAO);

        List<VisitType> dummyList = Lists.newArrayList();

        when(studyDao.getVisitTypes()).thenReturn(dummyList);
        
        final Map<String, List<?>> map = service.getStaticListsMap();

        final List<VisitType> actual = (List<VisitType>)map.get("visitTypes");
        
        assertEquals(dummyList, actual);
    }
}
