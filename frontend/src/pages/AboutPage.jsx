import Seo from '../components/Seo'

export default function AboutPage() {
  return (
    <article className="mx-auto max-w-3xl px-4 py-12">
      <Seo title="About" description="The For Creators story: traditional technique meeting contemporary form." />
      <h1 className="font-display text-4xl">Traditional meeting modern</h1>
      <p className="mt-6 leading-relaxed">
        For Creators started as a way to sell work without flattening it into “ethnic décor” or anonymous factory
        craft. We list both heritage lineages — Kanjeevaram, bidri, kalamkari, blue pottery — and studio pieces that
        borrow those grammars for apartments, offices, and everyday jewellery.
      </p>
      <p className="mt-4 leading-relaxed whitespace-pre-line">
        Checkout runs on Razorpay so UPI, cards, netbanking, and wallets all work. Unique kiln or
        loom pieces are marked one-of-a-kind and are not restocked when they sell.
      </p>
    </article>
  )
}
