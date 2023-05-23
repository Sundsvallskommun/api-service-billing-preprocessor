alter table issuer rename to recipient;

alter table recipient
    drop constraint fk_billing_record_id_issuer;

alter table recipient
    add constraint fk_billing_record_id_recipient
    foreign key (id)
    references billing_record (id);

alter table recipient
    add column legal_id varchar(255);

alter table recipient
    modify party_id varchar(255);