import dotenv from "dotenv";

// Tests run against the local Docker PostgreSQL configured in the repo-root .env
dotenv.config({ path: "../.env", quiet: true });
