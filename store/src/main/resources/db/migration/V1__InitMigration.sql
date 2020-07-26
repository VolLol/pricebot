create table goods_history_prices
(
    goods_info_id bigint    not null,
    price         int       not null,
    created_at    timestamp not null
);

create index goods_history_prices_main
    on goods_history_prices(goods_info_id,created_at);

create table goods_info
(
    id               bigserial             not null,
    telegram_user_id varchar               not null,
    title            varchar               not null,
    provider_url     varchar               not null,
    provider_type    varchar               not null,
    create_at        timestamp             not null,
    update_at        timestamp             not null,
    is_deleted       boolean default false not null
);

create unique index goods_info_provider_url_uindex
    on goods_info (provider_url, telegram_user_id);
