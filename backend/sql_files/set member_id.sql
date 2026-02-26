UPDATE board_table b
JOIN member_table m
ON b.board_writer = m.member_email
SET b.member_id = m.id;