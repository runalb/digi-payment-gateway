/**
 * Payment channel module: catalog, configuration, and channel-specific strategies.
 *
 * <ul>
 *   <li>{@code entity}, {@code repository}, {@code service}, {@code enums} — channel catalog</li>
 *   <li>{@code interfaces} — strategy interfaces (must not persist payments)</li>
 *   <li>{@code resolver} — Spring registry lookup by channel</li>
 *   <li>{@code impl} — channel-specific implementations</li>
 *   <li>{@code dto} — strategy request/response records</li>
 * </ul>
 */
package com.digirestro.digi_payment_gateway.payment_channel;
