package com.ii.testautomation.service.impl;

import com.ii.testautomation.dto.request.EmailRequest;
import com.ii.testautomation.dto.response.ExecutionHistoryResponse;
import com.ii.testautomation.entities.ExecutionHistory;
import com.ii.testautomation.entities.Project;
import com.ii.testautomation.repositories.ExecutionHistoryRepository;
import com.ii.testautomation.repositories.ProjectRepository;
import com.ii.testautomation.repositories.TestGroupingRepository;
import com.ii.testautomation.service.ExecutionHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Component
@PropertySource("classpath:MessagesAndCodes.properties")
public class ExecutionHistoryServiceImpl implements ExecutionHistoryService {
    @Autowired
    private ExecutionHistoryRepository executionHistoryRepository;
    @Autowired
    private TestGroupingRepository testGroupingRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${email.set.message}")
    private String emailMessage;
    @Value("${email.set.subject}")
    private String emailSubject;

    @Override
    public List<ExecutionHistoryResponse> viewByTestGroupingId(Long id) {
        List<ExecutionHistoryResponse> executionHistoryResponseList = new ArrayList<>();
        List<ExecutionHistory> executionHistoryList = executionHistoryRepository.findAllByTestGroupingIdOrderByCreatedAtDesc(id);
        for (ExecutionHistory executionHistory : executionHistoryList) {
            ExecutionHistoryResponse executionHistoryResponse = new ExecutionHistoryResponse();
            executionHistoryResponse.setTestGroupingId(id);
            BeanUtils.copyProperties(executionHistory, executionHistoryResponse);
            executionHistoryResponseList.add(executionHistoryResponse);
        }
        return executionHistoryResponseList;
    }

    @Override
    public String viewReportByExecutionHistoryId(Long id) throws IOException {
        ExecutionHistory executionHistory = executionHistoryRepository.findById(id).get();
        String reportName = executionHistory.getReportName();
        Long testGroupingId = executionHistory.getTestGrouping().getId();
        Long projectId = testGroupingRepository.findById(testGroupingId).get().getProject().getId();
        String path = projectRepository.findById(projectId).get().getProjectPath();
        Path reportPath = Path.of(path + File.separator + reportName.toString() + ".html");
        String myfile = Files.readString(reportPath);
        return myfile;
    }

    @Override
    public String viewReportWithLastUpdateByExecutionHistoryId(Long id) throws IOException {
        ExecutionHistory latestUpdate = executionHistoryRepository.findFirstByTestGroupingIdOrderByCreatedAtDesc(id);
        Long projectId = testGroupingRepository.findById(id).get().getProject().getId();
        String path = projectRepository.findById(projectId).get().getProjectPath();
        String reportName = latestUpdate.getReportName();
        Path reportpath = Path.of(path + File.separator + reportName.toString() + ".html");
        String reportContent = Files.readString(reportpath);
        return reportContent;
    }

    @Override
    public List<ExecutionHistoryResponse> executionHistoryDateFilter(Long id, Timestamp startDate, Timestamp endDate) {
        List<ExecutionHistoryResponse> executionHistoryResponseList = new ArrayList<>();
        List<ExecutionHistory> executionHistoryList = executionHistoryRepository.findByTestGroupingIdAndCreatedAtBetweenOrderByCreatedAtDesc(id, startDate, endDate);
        for (ExecutionHistory executionHistory : executionHistoryList) {
            ExecutionHistoryResponse executionHistoryResponse = new ExecutionHistoryResponse();
            BeanUtils.copyProperties(executionHistory, executionHistoryResponse);
            executionHistoryResponse.setTestGroupingId(id);
            executionHistoryResponseList.add(executionHistoryResponse);
        }
        return executionHistoryResponseList;
    }

    @Override
    public void emailHistoryReports(EmailRequest emailRequest) throws IOException, MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        for (Long id : emailRequest.getHistoryReportIds())
        {
            String name = executionHistoryRepository.findById(id).get().getReportName()+".html";
            mimeMessageHelper.addAttachment(name,addAttachment(id));
        }
        for (String toEmail : emailRequest.getToEmails())
        {
            mimeMessageHelper.addTo(toEmail);
            if (emailRequest.getSubject().isEmpty()) mimeMessageHelper.setSubject(emailSubject);
            else mimeMessageHelper.setSubject(emailRequest.getSubject());
            if (emailRequest.getMessage().isEmpty()) mimeMessageHelper.setText(emailMessage);
            else mimeMessageHelper.setText(emailRequest.getMessage());
        }
        javaMailSender.send(mimeMessage);
    }

    @Override
    public boolean existByTestGropingId(Long id) {
        return executionHistoryRepository.existsByTestGroupingId(id);
    }

    @Override
    public boolean existByExecutionHistoryId(Long id) {
        return executionHistoryRepository.existsById(id);
    }

    @Override
    public boolean deleteExecutionHistory(Long id, Long projectId) {
        Project projectOptional = projectRepository.findById(projectId).get();
        if (projectOptional != null) {
            String historyReport = projectOptional.getProjectPath() + File.separator + executionHistoryRepository.findById(id).get().getReportName().toString() + ".html";
            if (historyReport != null && !historyReport.isEmpty()) {
                if (deleteReport(historyReport)) {
                    executionHistoryRepository.deleteById(id);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean deleteReport(String filePath) {
        File fileToDelete = new File(filePath);
        if (fileToDelete.exists() && fileToDelete.isFile()) {
            return fileToDelete.delete();
        }
        return false;
    }

    private File addAttachment(Long id) throws IOException {
        ExecutionHistory executionHistory = executionHistoryRepository.findById(id).get();
        String reportName = executionHistory.getReportName();
        Long testGroupingId = executionHistory.getTestGrouping().getId();
        Long projectId = testGroupingRepository.findById(testGroupingId).get().getProject().getId();
        String path = projectRepository.findById(projectId).get().getProjectPath();
        Path reportPath = Path.of(path + File.separator + reportName.toString()+".html");
        File file = reportPath.toFile();
        return file;
    }
}
