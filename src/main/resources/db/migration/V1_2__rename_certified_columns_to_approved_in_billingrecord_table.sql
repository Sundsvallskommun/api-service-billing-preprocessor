
    alter table billing_record 
    change column certified approved datetime(6);
    
    alter table billing_record
    change column certified_by approved_by varchar(255);