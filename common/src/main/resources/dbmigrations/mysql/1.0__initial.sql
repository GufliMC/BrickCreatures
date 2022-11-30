-- apply changes
create table creatures (
  id                            varchar(40) not null,
  name                          varchar(255) not null,
  type                          varchar(255) not null,
  metadata                      varchar(8192),
  created_at                    datetime(6) not null,
  update_at                     datetime(6) not null,
  constraint uq_creatures_name unique (name),
  constraint pk_creatures primary key (id)
);

