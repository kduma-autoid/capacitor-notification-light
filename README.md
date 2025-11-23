# @kduma-autoid/capacitor-notification-light

Control the notification LED on the various Android devices which support it

## Install

```bash
npm install @kduma-autoid/capacitor-notification-light
npx cap sync
```

## API

<docgen-index>

* [`checkAvailability()`](#checkavailability)
* [`turnOn(...)`](#turnon)
* [`turnOff()`](#turnoff)
* [`echo(...)`](#echo)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### checkAvailability()

```typescript
checkAvailability() => Promise<LedInfo>
```

Check if LED lights are available on this device

**Returns:** <code>Promise&lt;<a href="#ledinfo">LedInfo</a>&gt;</code>

--------------------


### turnOn(...)

```typescript
turnOn(options: LedOptions) => Promise<void>
```

Turn on the notification LED with specified color and pattern

| Param         | Type                                              |
| ------------- | ------------------------------------------------- |
| **`options`** | <code><a href="#ledoptions">LedOptions</a></code> |

--------------------


### turnOff()

```typescript
turnOff() => Promise<void>
```

Turn off the notification LED

--------------------


### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

Echo test method (for development)

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### Interfaces


#### LedInfo

| Prop              | Type                  |
| ----------------- | --------------------- |
| **`available`**   | <code>boolean</code>  |
| **`lightsCount`** | <code>number</code>   |
| **`lightTypes`**  | <code>string[]</code> |


#### LedOptions

| Prop        | Type                                          |
| ----------- | --------------------------------------------- |
| **`color`** | <code><a href="#ledcolor">LedColor</a></code> |
| **`onMs`**  | <code>number</code>                           |
| **`offMs`** | <code>number</code>                           |


#### LedColor

| Prop        | Type                |
| ----------- | ------------------- |
| **`red`**   | <code>number</code> |
| **`green`** | <code>number</code> |
| **`blue`**  | <code>number</code> |

</docgen-api>
