import { createContext, useContext, useMemo, useState } from 'react'

const CartContext = createContext(null)
const STORAGE = 'fc_cart'

function load() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE) || '[]')
  } catch {
    return []
  }
}

export function CartProvider({ children }) {
  const [items, setItems] = useState(load)

  const persist = (next) => {
    setItems(next)
    localStorage.setItem(STORAGE, JSON.stringify(next))
  }

  const value = useMemo(
    () => ({
      items,
      add(product, quantity = 1) {
        const next = [...items]
        const existing = next.find((i) => i.productId === product.id)
        const max = product.oneOfAKind ? 1 : product.stockQuantity
        if (existing) {
          existing.quantity = Math.min(max, existing.quantity + quantity)
        } else {
          next.push({
            productId: product.id,
            name: product.name,
            price: product.price,
            image: product.image || product.images?.[0],
            quantity: Math.min(max, quantity),
            oneOfAKind: product.oneOfAKind,
          })
        }
        persist(next)
      },
      update(productId, quantity) {
        persist(items.map((i) => (i.productId === productId ? { ...i, quantity } : i)).filter((i) => i.quantity > 0))
      },
      remove(productId) {
        persist(items.filter((i) => i.productId !== productId))
      },
      clear() {
        persist([])
      },
      count: items.reduce((n, i) => n + i.quantity, 0),
      subtotal: items.reduce((n, i) => n + Number(i.price) * i.quantity, 0),
    }),
    [items],
  )

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}

export function useCart() {
  return useContext(CartContext)
}
