ALTER TABLE goods_info
    ALTER COLUMN telegram_user_id TYPE bigint USING (telegram_user_id::bigint);
