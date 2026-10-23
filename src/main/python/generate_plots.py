import os
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

os.makedirs('results/plots', exist_ok=True)

csv_path = 'results/results.csv'
if not os.path.exists(csv_path):
    raise FileNotFoundError(f"File {csv_path} not found! Run Benchmark.java first.")

df = pd.read_csv(csv_path)

sns.set_theme(style="whitegrid")
plt.rcParams.update({'font.size': 11})

plt.figure(figsize=(10, 6))
for alg in df['algorithm'].unique():
    subset = df[df['algorithm'] == alg]
    for density in subset['m'].div(subset['n']).unique():
        sub_d = subset[subset['m'] == subset['n'] * density]
label = f"{alg} (density {density}x)"
plt.loglog(sub_d['n'], sub_d['edge_scans'], marker='o', linestyle='-', label=label)

plt.xlabel('Number of Vertices ($n$ [log scale])', fontsize=12, fontweight='bold')
plt.ylabel('Edge Scans / Operations [log scale]', fontsize=12, fontweight='bold')
plt.title('Operations vs $n$ Across Algorithms and Densities', fontsize=14, fontweight='bold')
plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left', fontsize=9)
plt.tight_layout()
plt.savefig('results/plots/operations_vs_n.png', dpi=300)
plt.close()

plt.figure(figsize=(10, 6))
df['scans_per_edge'] = df['edge_scans'] / df['m']
for alg in df['algorithm'].unique():
    subset = df[df['algorithm'] == alg]
    plt.plot(subset['n'], subset['scans_per_edge'], marker='s', linestyle='-', label=alg)

plt.xscale('log')
plt.xlabel('Number of Vertices ($n$ [log scale])', fontsize=12, fontweight='bold')
plt.ylabel('Scans per Edge ($edgeScans / m$)', fontsize=12, fontweight='bold')
plt.title('Scans per Edge vs $n$ (Confirmation of Linear Work)', fontsize=14, fontweight='bold')
plt.legend(loc='upper right', fontsize=10)
plt.tight_layout()
plt.savefig('results/plots/scans_per_edge.png', dpi=300)
plt.close()

plt.figure(figsize=(10, 6))
kruskal_df = df[df['algorithm'] == 'Kruskal'].copy()

for label, group in kruskal_df.groupby(kruskal_df['m'] / kruskal_df['n']):
    dens_name = 'Sparse ($m=4n$)' if label == 4.0 else 'Dense ($m=20n$)'
    plt.plot(group['n'], group['edge_scans'] / group['m'], marker='^', linestyle='-', linewidth=2, label=dens_name)

plt.xscale('log')
plt.xlabel('Number of Vertices ($n$ [log scale])', fontsize=12, fontweight='bold')
plt.ylabel('Share of Edges Examined ($edgeScans / m$)', fontsize=12, fontweight='bold')
plt.title('Share of Edges Examined by Kruskal (Sparse vs Dense)', fontsize=14, fontweight='bold')
plt.legend(loc='lower right', fontsize=10)
plt.tight_layout()
plt.savefig('results/plots/kruskal_share_edges.png', dpi=300)
plt.close()

print("All three plots have been successfully created and saved in the results/plots/ directory!")