-- Catalog category/service images (run before prod deploy with ddl-auto=validate)

ALTER TABLE catalog_category
    ADD COLUMN IF NOT EXISTS image_url VARCHAR(2048);

CREATE TABLE IF NOT EXISTS catalog_service_image (
    id BIGSERIAL PRIMARY KEY,
    catalog_service_id BIGINT NOT NULL REFERENCES catalog_service (id),
    image_url VARCHAR(2048) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    "createdDateTime" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedDateTime" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_catalog_service_image_service_id
    ON catalog_service_image (catalog_service_id);
