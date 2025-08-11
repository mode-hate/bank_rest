ALTER TABLE transfers DROP CONSTRAINT fk_transfer_from_card;
ALTER TABLE transfers DROP CONSTRAINT fk_transfer_to_card;


ALTER TABLE transfers ALTER COLUMN from_card_id DROP NOT NULL;
ALTER TABLE transfers ALTER COLUMN to_card_id DROP NOT NULL;


ALTER TABLE transfers
  ADD CONSTRAINT fk_transfer_from_card FOREIGN KEY (from_card_id) REFERENCES cards(id) ON DELETE SET NULL;

ALTER TABLE transfers
  ADD CONSTRAINT fk_transfer_to_card FOREIGN KEY (to_card_id) REFERENCES cards(id) ON DELETE SET NULL;