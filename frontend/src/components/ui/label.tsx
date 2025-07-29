import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"

import { cn } from "@/lib/utils"

const labelVariants = cva(
  "inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ring-1 ring-inset",
  {
    variants: {
      variant: {
        default: "bg-gray-50 text-gray-600 ring-gray-500/10",
        bug: "bg-red-50 text-red-700 ring-red-600/10",
        feature: "bg-blue-50 text-blue-700 ring-blue-700/10",
        task: "bg-green-50 text-green-700 ring-green-600/10",
        story: "bg-purple-50 text-purple-700 ring-purple-700/10",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
)

export interface LabelProps
  extends React.HTMLAttributes<HTMLSpanElement>,
    VariantProps<typeof labelVariants> {
  color?: string
}

const Label = React.forwardRef<HTMLSpanElement, LabelProps>(
  ({ className, variant, color, children, ...props }, ref) => {
    const style = color ? { backgroundColor: `${color}20`, color: color } : {}
    
    return (
      <span
        ref={ref}
        className={cn(labelVariants({ variant }), className)}
        style={style}
        {...props}
      >
        {children}
      </span>
    )
  }
)
Label.displayName = "Label"

export { Label, labelVariants }