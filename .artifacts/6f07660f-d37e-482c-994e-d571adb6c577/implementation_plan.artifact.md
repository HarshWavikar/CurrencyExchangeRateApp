# Fix "Collection is empty" error for SQLDelight

The "Collection is empty" error in SQLDelight typically occurs when the plugin is applied but no database is defined in the `build.gradle.kts` file, or when no SQL schema files (`.sq`) are found.

## Proposed Changes

### [shared module](file:///Users/harsh/Documents/Harsh/CMP/Stevdza/CurrencyExchange/CurrencyExchange/shared/build.gradle.kts)

#### [MODIFY] [build.gradle.kts](file:///Users/harsh/Documents/Harsh/CMP/Stevdza/CurrencyExchange/CurrencyExchange/shared/build.gradle.kts)
- Add the `sqldelight` configuration block to define the database and its package.
- Add SQLDelight drivers for Android and iOS to their respective source sets.

#### [NEW] [currency.sq](file:///Users/harsh/Documents/Harsh/CMP/Stevdza/CurrencyExchange/CurrencyExchange/shared/src/commonMain/sqldelight/com/harshcode/currencyexchange/database/currency.sq)
- Create a placeholder SQL file to ensure the SQLDelight plugin has a schema to process.

## Verification Plan

### Automated Tests
- Run `./gradlew :shared:generateSqlDelightInterface` to verify that SQLDelight can generate the interfaces without errors.
- Run a Gradle sync to ensure the IDE no longer reports the "Collection is empty" error.
