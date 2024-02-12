package com.ii.testautomation.utils;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class DateAudit {
  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Timestamp createdAt;
  @LastModifiedDate
  @Column(nullable = false)
  private Timestamp updatedAt;
}
