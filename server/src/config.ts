// Local-only POC: bind to loopback by default (auth is a stub, see Docs/AI_CONTEXT.md)
export const config = {
  port: Number(process.env.PORT ?? 3000),
  host: process.env.HOST ?? "127.0.0.1",
  demo: process.env.DEMO === "true",
};
