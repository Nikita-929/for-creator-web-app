import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import api from '../api/client'
import { inr } from '../lib/format'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import Seo from '../components/Seo'

export function CartPage() {
  const { items, update, remove, subtotal } = useCart()
  const shipping = subtotal >= 5000 ? 0 : items.length ? 99 : 0
  return (
    <div className="mx-auto max-w-3xl px-4 py-10">
      <Seo title="Cart" />
      <h1 className="font-display text-4xl">Cart</h1>
      {items.length === 0 ? (
        <p className="mt-6">
          Your cart is empty.{' '}
          <Link className="underline" to="/shop">
            Continue shopping
          </Link>
        </p>
      ) : (
        <>
          <ul className="mt-8 divide-y divide-sand">
            {items.map((item) => (
              <li key={item.productId} className="py-4 flex gap-4">
                {item.image && <img src={item.image} alt="" className="h-24 w-20 object-cover" />}
                <div className="flex-1">
                  <p className="font-medium">{item.name}</p>
                  <p className="text-sm">{inr(item.price)}</p>
                  <label className="text-sm mt-2 inline-block">
                    Qty
                    <input
                      type="number"
                      min="1"
                      max={item.oneOfAKind ? 1 : 99}
                      value={item.quantity}
                      onChange={(e) => update(item.productId, Number(e.target.value))}
                      className="ml-2 w-16 border border-sand px-2 py-1"
                    />
                  </label>
                  <button type="button" className="ml-4 text-sm underline" onClick={() => remove(item.productId)}>
                    Remove
                  </button>
                </div>
              </li>
            ))}
          </ul>
          <p className="mt-6">Subtotal {inr(subtotal)}</p>
          <p className="text-sm text-sage">Shipping {shipping === 0 ? 'free' : inr(shipping)} (free over ₹5,000)</p>
          <Link to="/checkout" className="mt-6 inline-block bg-ink text-cream px-5 py-3 rounded">
            Checkout
          </Link>
        </>
      )}
    </div>
  )
}

function loadRazorpay() {
  return new Promise((resolve) => {
    if (window.Razorpay) {
      resolve(true)
      return
    }
    const script = document.createElement('script')
    script.src = 'https://checkout.razorpay.com/v1/checkout.js'
    script.onload = () => resolve(true)
    script.onerror = () => resolve(false)
    document.body.appendChild(script)
  })
}

export function CheckoutPage() {
  const { items, subtotal, clear } = useCart()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    shippingAddress: '',
    guestEmail: user?.email || '',
    guestName: user?.name || '',
  })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const shipping = subtotal >= 5000 ? 0 : 99

  function set(key, value) {
    setForm((f) => ({ ...f, [key]: value }))
  }

  async function verifyAndFinish(payload) {
    await api.post('/api/payments/verify', payload)
    clear()
    navigate(user ? '/account' : '/shop', { state: { paid: true } })
  }

  async function pay(e) {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      const { data } = await api.post('/api/checkout', {
        items: items.map((i) => ({ productId: i.productId, quantity: i.quantity })),
        shippingAddress: form.shippingAddress,
        guestEmail: user ? undefined : form.guestEmail,
        guestName: user ? undefined : form.guestName,
      })
      const demo = !data.razorpayKeyId || data.razorpayKeyId === 'rzp_test_demo' || String(data.razorpayOrderId).startsWith('order_demo_')
      if (demo) {
        await verifyAndFinish({
          razorpayOrderId: data.razorpayOrderId,
          razorpayPaymentId: 'pay_demo',
          razorpaySignature: 'demo',
        })
        return
      }
      const ok = await loadRazorpay()
      if (!ok) {
        setError('Could not load Razorpay. Check your connection.')
        return
      }
      const rzp = new window.Razorpay({
        key: data.razorpayKeyId,
        amount: Math.round(Number(data.total) * 100),
        currency: 'INR',
        name: 'For Creators',
        order_id: data.razorpayOrderId,
        prefill: { email: form.guestEmail, name: form.guestName },
        handler: async (response) => {
          await verifyAndFinish({
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
          })
        },
      })
      rzp.open()
    } catch (err) {
      setError(err.response?.data?.message || 'Checkout failed')
    } finally {
      setBusy(false)
    }
  }

  if (!items.length) {
    return (
      <p className="p-8">
        Nothing to check out.{' '}
        <Link className="underline" to="/shop">
          Shop
        </Link>
      </p>
    )
  }

  return (
    <div className="mx-auto max-w-xl px-4 py-10">
      <Seo title="Checkout" />
      <h1 className="font-display text-4xl">Checkout</h1>
      <p className="mt-2 text-sm text-sage">
        Guest checkout is available. Total {inr(subtotal + shipping)} including shipping.
      </p>
      <form className="mt-8 space-y-4" onSubmit={pay}>
        {!user && (
          <>
            <label className="block text-sm">
              Name
              <input required className="mt-1 w-full border border-sand px-3 py-2" value={form.guestName} onChange={(e) => set('guestName', e.target.value)} />
            </label>
            <label className="block text-sm">
              Email
              <input type="email" required className="mt-1 w-full border border-sand px-3 py-2" value={form.guestEmail} onChange={(e) => set('guestEmail', e.target.value)} />
            </label>
          </>
        )}
        <label className="block text-sm">
          Shipping address
          <textarea required rows="4" className="mt-1 w-full border border-sand px-3 py-2" value={form.shippingAddress} onChange={(e) => set('shippingAddress', e.target.value)} />
        </label>
        {error && <p className="text-clay">{error}</p>}
        <button disabled={busy} className="bg-clay text-cream px-5 py-3 rounded" type="submit">
          {busy ? 'Working…' : 'Pay with Razorpay'}
        </button>
        <p className="text-xs text-sage">Without Razorpay keys, payment completes in demo/test mode and inventory still decrements.</p>
      </form>
    </div>
  )
}
